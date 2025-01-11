package it.angelodesantis.security;

import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseFilter extends HttpServletResponseWrapper {
    private CharArrayWriter writer;

    public ResponseFilter(HttpServletResponse response) {
        super(response);
        // init writer
        this.writer = new CharArrayWriter();
    }

    // create a get method that returns a PrintWriter from writer
    public PrintWriter getWriter(){
        return new PrintWriter(writer);
    }
}
