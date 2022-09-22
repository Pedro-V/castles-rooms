/**
 * This class is the main class of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game. Users
 * can walk around some scenery. That's all. It should really be extended
 * to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game. It also evaluates and
 * executes the commands that the parser returns.
 * 
 * @author Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */
package jogo;
public class Game 
{
    private Parser parser;
    private Room currentRoom;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms() {
        Room entrada, grandeHall, sotao, biblioteca, enfermaria, porao, jardim, saladeJantar, igrejaAntiga;

        // create the rooms
        entrada = new Room("Entrada da masmorra");
        grandeHall = new Room("Saguão do castelo");
        sotao = new Room("Sotão empoeirado");
        biblioteca = new Room("Biblioteca antiga");
        enfermaria = new Room("Enfermaria");
        porao = new Room("Porão do castelo");
        jardim = new Room("Jardim de estátuas");
        saladeJantar = new Room("Grande sala de jantar");
        igrejaAntiga = new Room("Igreja mal assombrada");

        // initialise room exits
        entrada.setExit("leste", grandeHall);

        grandeHall.setExit("norte", biblioteca);
        grandeHall.setExit("leste", saladeJantar);

        biblioteca.setExit("norte", sotao);
        biblioteca.setExit("leste", jardim);
        biblioteca.setExit("sul", grandeHall);

        saladeJantar.setExit("oeste", grandeHall);
        saladeJantar.setExit("leste", enfermaria);
        saladeJantar.setExit("sul", porao);

        porao.setExit("norte", saladeJantar);

        enfermaria.setExit("norte", igrejaAntiga);
        enfermaria.setExit("oeste", saladeJantar);

        sotao.setExit("sul", biblioteca);

        jardim.setExit("oeste", biblioteca);

        igrejaAntiga.setExit("sul", enfermaria);

        currentRoom = entrada; // start game outside
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop. Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Obrigado por jogar! Até.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Bem vindo ao Castelo Poxim");
        System.out.println("Castelo Poxim é um jogo room-to-room de aventura baseado no terminal");
        System.out.println("Digite '" + CommandWord.HELP + "' se precisar de ajuda.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * 
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("Não entendi o que você quis dizer...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp() {
        System.out.println("Você está perdido, sozinho e caminha");
        System.out.println("pelo castelo.");
        System.out.println();
        System.out.println("Seus comandos são:");
        parser.showCommands();
    }

    /**
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Ir aonde?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("Não há porta nessa direção!");
        } else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * 
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Sair da onde?");
            return false;
        } else {
            return true; // sinaliza que queremos sair
        }
    }
}