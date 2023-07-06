package io.syscall.hsw.study.apiserver.infra.jpa.hibernate;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class DetailedEntityNotFoundDelegate implements EntityNotFoundDelegate {

    @Override
    public void handleEntityNotFound(String entityName, Object id) {
        throw new DetailedEntityNotFoundException(entityName, id);
    }

    public static class DetailedEntityNotFoundException extends EntityNotFoundException implements ErrorResponse {

        private final String entityName;

        private final Object id;

        public DetailedEntityNotFoundException(String entityName, Object id) {
            super("Unable to find " + entityName + " with id " + id);
            this.entityName = entityName;
            this.id = id;
        }

        @Override
        public HttpStatusCode getStatusCode() {
            return HttpStatus.NOT_FOUND;
        }

        @Override
        public ProblemDetail getBody() {
            return ProblemDetail.forStatusAndDetail(getStatusCode(), getMessage());
        }

        public String getEntityName() {
            return entityName;
        }

        public Object getId() {
            return id;
        }
    }
}
