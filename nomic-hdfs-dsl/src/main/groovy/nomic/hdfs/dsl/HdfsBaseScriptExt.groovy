package nomic.hdfs.dsl

import nomic.dsl.NomicBaseScript

/**
 * @author zdenko.vrabel@wirecard.com
 */
class HdfsBaseScriptExt {

    static void hdfs(final NomicBaseScript self, @DelegatesTo(HdfsDslContext) Closure c) {
        def context = new HdfsDslContext(self);
        c.delegate = context;
        c.call();
    }

}
