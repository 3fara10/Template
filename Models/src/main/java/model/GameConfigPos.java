package model;

import java.time.LocalDateTime;
import java.util.List;

public class GameConfigPos {
    public Integer id;
    public String player;
    public Integer score;
    public LocalDateTime gametime;
    public List<Configuration> configuration;
    public List<Answer> Answer;

    public GameConfigPos() {
    }

    public GameConfigPos(Game game, List<Configuration> config, List<Answer> pos){
        this.id = game.getId();
        this.player = game.getPlayer();
        this.score = game.getScore();
        this.gametime = game.getGametime();
        this.configuration = config;
        this.Answer = pos;
    }

    @Override
    public String toString() {
        return "model.GameConfigPos{" +
                "id=" + id +
                ", player='" + player + '\'' +
                ", score=" + score +
                ", gametime=" + gametime +
                ", configuration=" + configuration +
                ", model.Answer=" + Answer +
                '}';
    }
}
