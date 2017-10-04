# Nomic

Nomic is small tool/package manager that automatise our process of deployment and installation of analytics applications
into Hadoop ecosystem. The analytics are packaged into archive called *Box*. Each box has own descriptor file in 
root called `nomic.box` where is determined what and where is installed. The descriptor is based on Groovy with own DSL.

## Installation

### How to build

You can easily build distribution of nomic from source code by executing command project root:

    ./gradlew assembly
    
After command is succesfully executed, you can find ZIP, TAR and also RPM distribution in `nomic-dist/build/distributions` 
folder. Now you can continue with installation depends on distribution you pick.

### Standalone installation.

The standalone distribution is available as TAR and ZIP archives. All you need to do is just unzip/untar the archive 

    mkdir /where/to/install
    tar -xzf ./nomic-dist/build/distributions/nomic-*.tar -C /where/to/install

The application is installed into destination folder you choose. After that you can run the `nomic` application and 
you should see some output without error

    cd /where/to/install/nomic-{version}
    ./bin/nomic

### Linux installation from RPM

The Nomic application is also available as RPM you can easily install or deploy RedHat satellites. All you need is executing:

    rpm -Uvh ./nomic-dist/build/distributions/wd-nomic-*.noarch.rpm
    
Now you can type `nomic` in shell. You should see the nomic's output. The configuration is placed in `/etc/nomic`. 
The main application is placed in `/usr/share/nomic` folder.        

## Nomic application

TODO

## Nomic DSL

In this section you will find informations how to write `nomic.box` descriptors. The descriptor is simple text
file. We're using groovy as main language here and we created declarative DSL. The minimum descriptor file declare
the box name, the box group and version. 

```groovy
group = "app"
name = "some-box"
version = "1.0.0"
```

In your descriptor files, you can use some useful global variables:

* **group** box group can be any string. You can also structure groups with `/` character.
* **name** is box name that must be unique because it's used for identification
* **version** box version
* **user** username the nomic is using for installation/uploading files into HDFS configured in `nomic.conf`
* **homeDir** each used in HDFS might have his own home directory. It's usefull when you want to sandboxing your applications/analyses.
* **appDir** path to application directory in HDFS where are applications installed. The default value is ${homeDir}/app.
* **defaultSchema** contain default HIVE schema that is configure via `nomic.conf`. Also good if you want to sandboxing your apps.

### Hdfs

In `hdfs` section you can declare where files from you box will be uploaded into HDFS. Let's imagine we've got
box archive like:

    /nomic.box
    /some-file.xml

The descriptor below will install the `some-file.xml` into application's folder (depends how it's configured).

```groovy
group = "app"
name = "some-box"
version = "1.0.0"

hdfs {
    resource 'some-file.xml'
}
```

With small modification you can place any resource to any path. E.g. following example will demonstrate how to
place some file to root `/app`.

```groovy
hdfs {
    resource 'some-file.xml' to '/app/workflow.xml'
}
```

If you dont' place `/` character, the file will be paced into working directory that is basically `${appDir}`.


```groovy
hdfs {
    resource 'some-file.xml' to 'workflows/some-workflow.xml'
}
```
The example above will ensure the file in `${appDir}/workflows/some-workflow.xml` where the `some-file.xml` content 
will be copied.

#### Directories

You can also declare presence of directory. The declaration will create empty new directory if is not present yet.

```groovy
hdfs {
    dir "data"
}
```

Because path start without `/` character, the directory will be created in current working directory. This declaration
also ensure uninstalling that means the folder will be removed when uninstall or upgrade. If you wish to keep it, you 
can use the `keepIt` parameter:

```groovy
hdfs {
    dir "data" keepIt true
}
```

#### Templates

You can use also templates in your box. The template is basically any text file where placeholders will be replaced by
values from descriptor. Nomic is using internally 'Mustache' as template engine. The syntax of declaration is very simple:

    template {source} to {dest} with {fields} 

The `to` is optional same as in `resource` case. The simple template example is:

```groovy
hdfs {
    template "simple-template.xml.mustache" with 'title': 'Clean Code', 'author': 'Robert C. Martin'
}
``` 

The *simple-template.xml.mustache* template can be:

```xml
<book>
    <title>{{title}}</title>
    <author>{{author}}</author>
</book>
```

After installing the box, the template's placeholders will be replaced with values from descriptor and file will be placed
by default to working directory without `.mustache` postfix. You can declare custom path by `to`.

### Hive 

You can declare in descriptor also the HIVE components. You can declare tables, schemes, you can also ensure the Hive 
scripts executions. Everything for Hive must be wrapped in `hive` section. Following example show how to create
simple table in default schema you have configured in `nomic.conf`:

```groovy
group = "app"
name = "some-box"
version = "1.0.0"

hive {
    table 'authors' from "create_authors_table.q"
}
```

In you box, you need to have the hive qurey file `create_authors_table.q` that will create table if it's not present in
system:

```sql
CREATE EXTERNAL TABLE authors(
  NAME STRING,
  SURNAME STRING
)
STORED AS PARQUET
LOCATION '/data/authors';
``` 

#### Placeholders in scripts

In your hive scripts you can use placeholders they will be replaced with values from descriptor. Values are declared via
`fields`. This is sometime usefull when you want e.g. place table into some schema.

```groovy
hive {
    fields 'APP_DATA_DIR': "${appDir}/data", 'DATABASE_SCHEMA': defaultSchema
    
    table 'authors' from "create_authors_table.q"
}  
```

The `create_authors_table.q` then use these placeholders:

```sql
CREATE EXTERNAL TABLE ${DATABASE_SCHEMA}.authors(
  NAME STRING,
  SURNAME STRING
)
STORED AS PARQUET
LOCATION '${APP_DATA_DIR}/authors';
```

#### Multiple schemas

You can also declare multiple schemas with custom names. Also you can build a schema name as composition of various
values.

```groovy
hive("${user}_${name}_staging") {
    table 'some_table' from 'some_script.q'
}

hive("${user}_${name}_processing") {
    fields 'DATABASE_SCHEMA': "${user}_${name}_processing"  
    table 'some_table' from 'some_script.q'
}

hive("${user}_${name}_archive") {
    table 'some_table' from 'some_script.q'
}
```
This descriptor script will ensure 3 schemas where name of schema will be created as composition of user name, box name
and some postfix. As you can see, each section might have own `fields` declaration.


## For developers