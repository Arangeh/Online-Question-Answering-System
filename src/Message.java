import java.io.*;

public class Message implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final int SEARCHByQuestion = 0, LOGIN = 1, LOGOUT = 2, REGISTER = 3, QUESTION = 4 , ANSWER = 5 , COMMENT = 6  , CheckUserId = 7 ,CheckEmail = 8,
	GetQuestion = 9 , AddCommentQ = 10 , AddCommentA = 11 , SearchByTime = 12 , UpdateA = 13 , UpdateQ = 14 , UpdateC = 15, ShowA = 16 , RateU = 34
	,ShowQ = 17 , ShowC = 18 , DeleteA = 19 , DeleteQ = 20 , DeleteC = 21 ,RateA = 22 , RateQ = 23 , DeleteAccount = 24 , EditedA = 25 , withoutAnswerQ = 35
	,EditedQ = 26 , PrivateFromNormal = 27 , SeeEditA = 28 , SeeEditQ = 29 , AnswerEditQ = 30 , AnswerEditA = 31 , SearchByQuestionN = 32 , SearchByTimeN = 33;
    private int type;
    private String message;

    // constructor
    Message(int type, String message) {
        this.type = type;
        this.message = message;
    }

    // getters
    int getType() {
        return type;
    }
    
    String getMessage() {
        return message;
    }
    
}

