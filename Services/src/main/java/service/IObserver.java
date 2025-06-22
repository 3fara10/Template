package service;


public interface IObserver {
    void handleEvent(ServiceEvent serviceEvent, Object data) throws ServiceException;
}
