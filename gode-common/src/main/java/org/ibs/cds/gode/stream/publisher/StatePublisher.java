package org.ibs.cds.gode.stream.publisher;

import org.ibs.cds.gode.stream.config.StreamSourceType;

public interface StatePublisher {
    StreamSourceType type();
    void publish();
    boolean isPublishing();
}
