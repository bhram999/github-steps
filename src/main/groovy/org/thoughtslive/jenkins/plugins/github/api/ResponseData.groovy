package org.thoughtslive.jenkins.plugins.github.api

import groovy.transform.Canonical
import groovy.transform.builder.Builder

@Canonical
@Builder
class ResponseData<T> implements Serializable {

    private static final long serialVersionUID = 7846727738826103343L

    boolean successful

    int code

    String message

    String error

    T data
}
