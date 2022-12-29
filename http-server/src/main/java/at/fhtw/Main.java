package at.fhtw;

import at.fhtw.httpserver.server.Service;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.httpserver.server.Server;
import at.fhtw.sampleapp.service.card.CardService;
import at.fhtw.sampleapp.service.deck.DeckService;
import at.fhtw.sampleapp.service.echo.EchoService;
import at.fhtw.sampleapp.service.pckg.PackageService;
import at.fhtw.sampleapp.service.session.SessionService;
import at.fhtw.sampleapp.service.user.UserService;
import at.fhtw.sampleapp.service.weather.WeatherService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        // TODO: unitOfWork placement besser machen
        Router router = new Router();
        router.addService("/weather", new WeatherService());
        router.addService("/echo", new EchoService());
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions", new PackageService());
        router.addService("/cards", new CardService());
        router.addService("/deck", new DeckService());
        return router;
    }
}
