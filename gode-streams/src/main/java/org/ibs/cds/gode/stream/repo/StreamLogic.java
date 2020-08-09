package org.ibs.cds.gode.stream.repo;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.ibs.cds.gode.stream.DataProcessor;

public class StreamLogic {

    static  {
        processors = HashBasedTable.create();
    }

    private static Table<String, String, DataProcessor<?,?,?>> processors;


    public static <T extends DataProcessor<?,?,?>> void addProcessor(String source, String process, T processor){
        processors.put(source, process, processor);
    }

    public static <T extends DataProcessor<?,?,?>> T getProcessor(String source, String process){
        return (T) processors.get(source, process);
    }
}
