package service;

import model.Game;
import model.User;

import java.util.Optional;

public interface IGameService extends IObservable,IService {
    Game startNewGame(String playerUsername, IObserver clientObserver) throws ServiceException;
}
