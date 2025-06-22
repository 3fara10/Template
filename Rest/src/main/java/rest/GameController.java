package rest;


import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import persistance.IAnswerRepository;
import persistance.IConfigurationRepository;
import persistance.IGameRepository;
//import persistance.IPositionsRepository;
import persistance.IUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/games")
public class GameController {
    private final IGameRepository gameRepository;
    private final IAnswerRepository positionsRepository;
    private final IConfigurationRepository configurationRepository;

    @Autowired
    public GameController(IGameRepository gameRepository, IAnswerRepository positionsRepository, IConfigurationRepository configurationRepository) {
        this.gameRepository = gameRepository;
        this.positionsRepository = positionsRepository;
        this.configurationRepository = configurationRepository;
    }

    @RequestMapping(value = "/{player}/{idgame}", method = RequestMethod.GET)
    public GameConfigPos getAll(@PathVariable String player, @PathVariable Integer idgame){
        Game[] games = gameRepository.getAll().toArray(Game[]::new);
        int neededGamePos = 0;
        int neededConfigPos = 0;
        int neededPosPos = 0;
        for(int i = 0; i < games.length; i ++){
            if(Objects.equals(games[i].getId(), idgame) && Objects.equals(games[i].getPlayer(), player))
                neededGamePos = i;
        }
        Configuration[] configs = configurationRepository.getAll().toArray(Configuration[]::new);
        List<Configuration> configList = new ArrayList<>();
        Answer[] positions = positionsRepository.getAll().toArray(Answer[]::new);
        List<Answer> answerList = new ArrayList<>();
        for(int i = 0; i < configs.length; i ++){
            if(Objects.equals(configs[i].getGameid(), idgame)) {
                neededConfigPos = i;
                configList.add(configs[i]);
            }
        }
        for(int i = 0; i < positions.length; i ++){
            if(Objects.equals(configs[i].getGameid(), idgame)) {
                neededPosPos = i;
                answerList.add(positions[i]);
            }
        }
//        return new GameConfigPos();
        GameConfigPos gameConfigPos = new GameConfigPos(games[neededGamePos], configList, answerList);
        return gameConfigPos;
    }

}
