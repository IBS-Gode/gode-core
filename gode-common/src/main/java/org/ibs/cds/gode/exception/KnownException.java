package org.ibs.cds.gode.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;

public enum KnownException {

    FAILED_TO_START(1,"Failed to start app"){
        private String message = getMessage();
        public  GodeStartupException provide(Throwable e, Serializable details) {
            return new GodeStartupException(new Error(getCode(), message, details), message, e);
        }

        public GodeStartupException provide(Serializable details) {
            return new GodeStartupException(new Error(getCode(), message, details), message);
        }
    },
    OBJECT_NOT_FOUND(2,"Specific object not found in system"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), message, details),message, e);
        }

        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    ENTITY_VALIDATIONS_FAILED(3, "Entity validation failed"){
        private String message = getMessage();
        @Override
        public EntityValidationFailedException provide(Serializable details) {
            return new EntityValidationFailedException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public EntityValidationFailedException provide(Throwable e, Serializable details) {
            return new EntityValidationFailedException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    ASSERTION_FAILED(4, "Assertion failed"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    SYSTEM_FAILURE(5, "System failure"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    SAVE_FAILED(6, "Entity save failed"){
        private String message = getMessage();
        @Override
        public EntityMutationOperationFaiednException provide(Serializable details) {
            return new EntityMutationOperationFaiednException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public EntityMutationOperationFaiednException provide(Throwable e, Serializable details) {
            return new EntityMutationOperationFaiednException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    QUERY_FAILED(7, "Entity query failed"){
        private String message = getMessage();
        @Override
        public EntityQueryOperationFaiedException provide(Serializable details) {
            return new EntityQueryOperationFaiedException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public EntityQueryOperationFaiedException provide(Throwable e, Serializable details) {
            return new EntityQueryOperationFaiedException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    ENTITY_OPERATION_FAILED(8, "Entity process failed"){
        private String message = getMessage();
        @Override
        public EntityOperationFailedException provide(Serializable details) {
            return new EntityOperationFailedException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public EntityOperationFailedException provide(Throwable e, Serializable details) {
            return new EntityOperationFailedException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    AUTHORISATION_EXCEPTION(8, "Authorisation failed"){
        private String message = getMessage();
        @Override
        public AuthException provide(Serializable details) {
            return new AuthException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public AuthException provide(Throwable e, Serializable details) {
            return new AuthException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    SERILISATION_EXCEPTION(9, "Serialization/Deserialization exception"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    QUEUE_PUSH_FAILED_EXCEPTION(10, "Push to queue failed"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    QUEUE_CONSUMPTION_FAILED_EXCEPTION(11, "Consumption from queue failed"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    MEDIA_NOT_FOUND_EXCEPTION(12, "No media found"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    RELATIVE_NOT_FOUND_EXCEPTION(12, "Relative is not found in relationship"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    HTTP_EXCEPTION(13, "Failed to invoke api call"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    INVALID_RANDOM_CONFIG(14, "Failed to generate random number due to invalid requirement"){
        private String message = getMessage();
        @Override
        public GodeRuntimeException provide(Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public GodeRuntimeException provide(Throwable e, Serializable details) {
            return new GodeRuntimeException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    APP_VALIDATIONS_FAILED(15, "App validation failed"){
        private String message = getMessage();
        @Override
        public AppValidationFailedException provide(Serializable details) {
            return new AppValidationFailedException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public AppValidationFailedException provide(Throwable e, Serializable details) {
            return new AppValidationFailedException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },
    INVALID_CONFIG_EXCPETION(16, "Invalid configuration exception"){
        private String message = getMessage();
        @Override
        public InvalidConfigurationException provide(Serializable details) {
            return new InvalidConfigurationException(new Error(getCode(), getMessage(), details), message);
        }

        @Override
        public InvalidConfigurationException provide(Throwable e, Serializable details) {
            return new InvalidConfigurationException(new Error(getCode(), getMessage(), details),getMessage());
        }
    },

    ;

    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    KnownException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private static String details(Throwable e){
        return "Message: ".concat(ExceptionUtils.getMessage(e)).concat("| Root cause message: ").concat(ExceptionUtils.getRootCauseMessage(e));
    }

    public <T extends GodeException>  T provide(Throwable e){
        return provide(e, details(e));
    }
    public <T extends GodeException>  T provide(){
        return provide((String) null);
    }
    public abstract <T extends GodeException,Details extends Serializable>  T provide(Details details);
    public abstract <T extends GodeException,Details extends Serializable>  T provide(Throwable e, Details details);

}
