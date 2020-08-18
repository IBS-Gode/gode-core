package org.ibs.cds.gode.stream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.ibs.cds.gode.stream.DataProcessor;

import java.util.function.Supplier;

public abstract class GodeStreamLogic {

    private Table<String, String, Supplier<? extends DataProcessor<?,?>>> processors;

    public GodeStreamLogic() {
        this.processors = HashBasedTable.create();
    }


    public <T extends DataProcessor<?,?>> void addProcessor(String from, String to, Supplier<T> processor){
        processors.put(from, to, processor);
    }

    public <T extends DataProcessor<?,?>> Supplier<T> getProcessor(String from, String to){
        return (Supplier<T>) processors.get(from, to);
    }
}
