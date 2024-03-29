package at.fhtw.sampleapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.CardRepository;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.Card;

import java.util.Collection;

public class CardController extends Controller {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardController() {
        this.cardRepository = new CardRepository();
        this.userRepository = new UserRepository();
    }

    public Response showCards(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String currentToken = request.getCurrentToken();
        if (currentToken == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given\" }"
            );
        }
        try (unitOfWork){
            int player_id = this.userRepository.getPlayerId(currentToken, unitOfWork);
            Collection<Card> cardData = this.cardRepository.getCardsWithId(player_id, unitOfWork);
            if (cardData.isEmpty()) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        // NO_CONTENT DOESN'T WORK
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\": \"You have no cards\" }"
                );
            }
            String cardDataJSON = this.getObjectMapper().writeValueAsString(cardData);

            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    (cardDataJSON + "\n")
            );
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}
