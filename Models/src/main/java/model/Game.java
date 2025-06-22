package model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name ="games")
public class Game implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name= "player_id")
    private Integer player;

    @Column(name = "score")
    private Integer score;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gametime")
    LocalDateTime gametime;

    public Game() {
    }

    public Game(Integer id, Integer player, Integer score, LocalDateTime gametime) {
        this.id = id;
        this.player = player;
        this.score = score;
        this.gametime = gametime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlayerId() {
        return player;
    }

    public void setPlayerId(Integer player) {
        this.player = player;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getGametime() {
        return gametime;
    }

    public void setGametime(LocalDateTime gametime) {
        this.gametime = gametime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) && Objects.equals(player, game.player) && Objects.equals(score, game.score) && Objects.equals(gametime, game.gametime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, player, score, gametime);
    }

    @Override
    public String toString() {
        return "model.Game{" +
                "id=" + id +
                ", player='" + player.toString() + '\'' +
                ", score=" + score +
                ", gametime=" + gametime +
                '}';
    }
}
