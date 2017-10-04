/****************************************************************************************************************
 *
 * Example of nomic.groovy
 *
 * You can use several global variables:
 *    user - the name of user the nomic is acting as (e.g. zdenko.vrabel or hdfs. Can be configured in nomic.conf)
 *    homeDir - the path to user's home dir (e.g. /user/zdenko.vrabel)
 *    appDir - path to '/app' folder that comes from nomic configuration (default value is /user/{user}/app)
 *    defaultSchema - name of default schema that will be used by HIVE (default value is DEFAULT)
 *
 ****************************************************************************************************************/
group = "nomic-examples" // optional
name = "hello-world" // mandatory
version = "2.0.0" // mandatory

module 'sub-module' // install the submodule. The submodule is folder where is another nomic.groovy - inspiration comes from maven's <module>.

// example of 'if' condition
if ("hdfs".equalsIgnoreCase(user)) {
    dir "/data/authors/input" // create empty dir '/data/authors/input'. Uninstall do nothing, the folder stay.
} else {
    dir path:"${homeDir}/data/input", removable: true // create empty dir. The uninstall will delete the folder and folder's content
}

resource "some-file.xml" // this resource will be copied into hdfs, into ${appDir}/${group}/{$name}/some-file.xml
resource "another-file.csv" to "/data/some-dir/some-file.csv" // this resource will be copied to specific folder
template "some-template.xml.mustache" with field1: 'value1', field2: 'value2' // create the new file as ${appDir}/${group}/{$name}/some-template.xml where placeholder fields will be replaced by values

hive {
    fields 'OMNI_ROOT_DIR': "${appDir}/..", 'DATABASE_SCHEMA': defaultSchema //this part declare the fields and values they will be replaced in *.Q scripts.

    script 'create_authors_table.q' // this will invoke hive script during installation, it's skipped during uninstall
    table 'books' being 'create_books_table.q' //create table 'books'. The table is defined in hive script. The table is dropped during uninstall
}