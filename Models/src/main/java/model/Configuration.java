package model;

import java.io.Serializable;
import java.util.Objects;

public class Configuration implements Serializable {
    private Integer id;
    private Integer gameid;
    private String configuration;

    private String answer;

    private String difficulty;

    private Integer questionpoints;

    public Integer getQuestionpoints() {
        return questionpoints;
    }

    public void setQuestionpoints(Integer questionpoints) {
        this.questionpoints = questionpoints;
    }

    public Configuration(Integer id, Integer gameid, String configuration, String answer, String difficulty, Integer questionpoints) {
        this.id = id;
        this.gameid = gameid;
        this.configuration = configuration;
        this.answer = answer;
        this.difficulty = difficulty;
        this.questionpoints = questionpoints;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Configuration(Integer id, Integer gameid, String configuration, String answer, String difficulty) {
        this.id = id;
        this.gameid = gameid;
        this.configuration = configuration;
        this.answer = answer;
        this.difficulty = difficulty;
    }

    public Configuration(Integer id, Integer gameid, String configuration, String answer) {
        this.id = id;
        this.gameid = gameid;
        this.configuration = configuration;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public Configuration() {
    }

    public Configuration(Integer id, Integer gameid, String configuration) {
        this.id = id;
        this.gameid = gameid;
        this.configuration = configuration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameid() {
        return gameid;
    }

    public void setGameid(Integer gameid) {
        this.gameid = gameid;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(id, that.id) && Objects.equals(gameid, that.gameid) && Objects.equals(configuration, that.configuration) && Objects.equals(answer, that.answer) && Objects.equals(difficulty, that.difficulty) && Objects.equals(questionpoints, that.questionpoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameid, configuration, answer, difficulty, questionpoints);
    }

    @Override
    public String toString() {
        return "model.Configuration{" +
                "id=" + id +
                ", configuration='" + configuration + '\'' +
                '}';
    }
}
