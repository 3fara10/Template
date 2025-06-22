package model;

import java.io.Serializable;
import java.util.Objects;

public class Answer implements Serializable {

    private Integer id;
    private Integer gameid;
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Answer() {
    }

    public Answer(Integer id, Integer gameid, String positions) {
        this.id = id;
        this.gameid = gameid;
        this.answer = positions;
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

    public String getPositions() {
        return answer;
    }

    public void setPositions(String positions) {
        this.answer = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(id, answer1.id) && Objects.equals(gameid, answer1.gameid) && Objects.equals(answer, answer1.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gameid, answer);
    }

    @Override
    public String toString() {
        return "Positions{" +
                "id=" + id +
                ", positions='" + answer + '\'' +
                '}';
    }
}
