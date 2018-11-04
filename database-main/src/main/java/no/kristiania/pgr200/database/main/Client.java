package no.kristiania.pgr200.database.main;

public class Client {


    public static void main(String[] args) {
        Client client = new Client();

        String command = args[0];
        switch (command) {
            case "add": {
            for(int i = 0; i > args.length; i++){


            }
            }
            case "list": {

            }
            case "get": {

            }

        }
    }




    /*
    Adds a talk to the core with the option to add the time and place of the talk
    Talk: Title, Description, Topic
    Day: dd-MM-YYYY
    Room: varchar(10)
    Timeslot: start and finish time
     */
    public void addTalk() {

    }


    /*
    Lists all talks registered based on topic
     */
    public void listTalks() {

    }

    /*
    Gets the details of a specific talk based on id
     */
    public void getTalk() {

    }

    /*
    Updates the values of a talk in the core

     */
    public void updateTalk() {

    }


}
