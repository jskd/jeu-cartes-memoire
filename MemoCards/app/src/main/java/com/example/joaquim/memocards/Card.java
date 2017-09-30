package com.example.joaquim.memocards;

/**
 * Created by Joaquim on 11/12/2016.
 */

public class Card {

    private int id = -1;
    private String question;
    private String answer;
    private int priority;

    public Card(String question, String answer, int priority){
        this.question = question;
        this.answer = answer;
        this.priority = priority;
    }

    public void setId(int id){ this.id = id; }

    public int getId(){ return this.id; }

    public String getQuestion(){ return this.question; }

    public String getAnswer(){ return this.answer; }

    public int getPriority(){ return this.priority; }

}
