package de.chkal.togglz.console;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {

    boolean handles(String path);

    void process(HttpServletRequest request, HttpServletResponse response) throws IOException;

}