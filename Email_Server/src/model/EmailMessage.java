/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author tom18
 */
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String subject;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private List<String> files;
    private String content;

    public EmailMessage() {
    }

    public EmailMessage(String id, String name, String subject, String from, String to, String content) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public EmailMessage(String id, String name, String subject, String from, String to, String cc, String bcc, List<String> files, String content) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.files = files;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public List<String> getFile() {
        return files;
    }

    public void setFile(List<String> files) {
        this.files = files;
    }

}
