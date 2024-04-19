package org.example.models;

import java.util.Objects;

public class Formulairej {
    private int idf ;
    private String question ;
    private String reponse ;

    public Formulairej(int idf, String question, String reponse) {
        this.idf = idf;
        this.question = question;
        this.reponse = reponse;
    }

    public int getIdf() {
        return idf;
    }

    public void setIdf(int idf) {
        this.idf = idf;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formulairej)) return false;
        Formulairej that = (Formulairej) o;
        return getIdf() == that.getIdf() && Objects.equals(getQuestion(), that.getQuestion()) && Objects.equals(getReponse(), that.getReponse());
    }

}
