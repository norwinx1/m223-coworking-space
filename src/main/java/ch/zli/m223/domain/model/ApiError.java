package ch.zli.m223.domain.model;

public class ApiError {
    private String error;

    /**
     * @param error
     */
    public ApiError(String error) {
        this.error = error;
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
