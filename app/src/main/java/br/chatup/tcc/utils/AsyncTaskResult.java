package br.chatup.tcc.utils;

/**
 * Created by Luan on 9/10/2016.
 */
public class AsyncTaskResult {
    private Exception error;

    public Exception getError() {
        return error;
    }

    public AsyncTaskResult(Exception error) {
        super();
        this.error = error;
    }
}