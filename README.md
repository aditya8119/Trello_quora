### Developers
1. Aditya Balasubramanyam <br/>
Github Account : adbalasu (Primary) , aditya8119 (For checking in Stub Code)<br/>

2. Soumya Rout<br/>
Github Account : soumyarout80<br/>

3. Swetha Gurram<br/>
Github Account : sri-swetha<br/>

4. Urmila Unni<br/>
Github Account : urmilaunni88<br/>


### Trello_quora

This project is a basic implementation for the backend for a Question Answer Forum.<br/>

The following REST API's have been implemented :<br/>

1. signup - "/user/signup"
2. signin - "/user/signin"
3. signout - "/user/signout"
4. userProfile - "/userprofile/{userId}"
5. userDelete - "/admin/user/{userId}"
6. createQuestion - "/question/create"
7. getAllQuestions - "/question/all"
8. editQuestionContent - "/question/edit/{questionId}"
9. deleteQuestion - "/question/delete/{questionId}"
10. getAllQuestionsByUser - "question/all/{userId}"
11. createAnswer - "/question/{questionId}/answer/create"
12. editAnswerContent - "/answer/edit/{answerId}"
13. deleteAnswer - "/answer/delete/{answerId}"
14. getAllAnswersToQuestion - "answer/all/{questionId}"

### Specifications :
JDK Version : 1.8.0_241<br/>
Spring Boot : 2.0.1<br/>
Spring Framework : 5.0.8<br/>
Spring Fox : 2.7.0<br/>
Database : Postgre SQL<br/>
Database Driver Version : 42.2.2<br/>
Build Tool : Apache Maven <br/>

### To Build first time (includes Database Setup)
Navigate into the folder quora-db :<br/>
mvn clean install -Psetup

### To Build<br/>
mvn clean install<br/>

### To Build without Tests<br/>
mvn clean install -DskipTests<br/>


