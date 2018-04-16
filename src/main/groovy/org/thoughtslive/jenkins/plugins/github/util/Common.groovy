package org.thoughtslive.jenkins.plugins.github.util

import org.thoughtslive.jenkins.plugins.github.api.ResponseData
import retrofit2.Response

/**
 * Common utility functions.
 **/
class Common {

    /**
     * Converts Retrofit's {@link Response} to {@link ResponseData}
     *
     * @param response instance of {@link Response}
     * @return an instance of {@link ResponseData}
     */
    static <T> ResponseData<T> parseResponse(final Response<T> response) throws IOException {
        def builder = ResponseData.builder()
        builder.successful(response.isSuccessful()).code(response.code()).message(response.message())
        if (!response.isSuccessful()) {
            final String errorMessage = response.errorBody().string()
            builder.error(errorMessage)
        } else {
            builder.data(response.body())
        }
        return builder.build()
    }

    /**
     * Builds error response from the given exception.
     *
     * @param e instance of {@link Exception}
     * @return an instance of {@link ResponseData}
     */
    static <T> ResponseData<T> buildErrorResponse(final Exception e) {
        def builder = ResponseData.builder()
        final String errorMessage = getRootCause(e).getMessage()
        return builder.successful(false).code(-1).error(errorMessage).build()
    }

    /**
     * Returns actual Cause from the given exception.
     *
     * @return {@link Throwable}
     */
    static Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() != null) {
            return getRootCause(throwable.getCause())
        }
        return throwable
    }

    /**
     * Write a message to the given print stream.
     *
     * @param logger {@link PrintStream}
     * @param message to log.
     */
    static void log(final PrintStream logger, final Object message) {
        if (logger != null) {
            logger.println(message)
        }
    }

    /**
     * Empty check for string.
     *
     * @return true if given string is null or empty.
     */
    static boolean empty(final String str) {
        return str == null || str.trim().isEmpty()
    }

}
