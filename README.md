# Developers
1. Aditya Balasubramanyam
Github Account : adbalasu (Primary) , aditya8119 (For checking in Stub Code)

2. Soumya Rout
Github Account : soumyarout80

3. Swetha Gurram
Github Account : sri-swetha

4. Urmila Unni
Github Account : urmilaunni88


# Trello_quora

This project is a basic implementation for the backend for a Question Answer Forum.

The following REST API's have been implemented :

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

# Specifications :
JDK Version : 1.8.0_241
Spring Boot : 2.0.1
Spring Framework : 5.0.8
Spring Fox : 2.7.0
Database : Postgre SQL
Database Driver Version : 42.2.2
Build Tool : Apache Maven 

# To Build first time (includes Database Setup)
Navigate into the folder quora-db
mvn clean install -Psetup

# To Build
mvn clean install

#To Build without Tests
mvn clean install -DskipTests


