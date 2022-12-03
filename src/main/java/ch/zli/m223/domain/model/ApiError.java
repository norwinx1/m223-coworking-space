package ch.zli.m223.domain.model;

import javax.ws.rs.core.Response.Status;

public class ApiError {
    private Status status;
    private String error;

    /**
     * @param status
     * @param error
     */
    public ApiError(Status status, String error) {
        this.status = status;
        this.error = error;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

}
