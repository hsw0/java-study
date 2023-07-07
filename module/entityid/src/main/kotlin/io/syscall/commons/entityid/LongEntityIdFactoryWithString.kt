package io.syscall.commons.entityid

public interface LongEntityIdFactoryWithString<E : LongEntityId> :
    LongEntityIdFactory<E>,
    FromStringEntityIdFactory<Long, E>
