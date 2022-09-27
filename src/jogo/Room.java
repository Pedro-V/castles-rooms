package jogo;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Room 
{
    private String description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    /** O atributo funciona como um indicativo de quais características especiais 
     * aquela sala possui.
     */
    private Attribute atributo;
    private boolean atributo_ativo;
    // As perguntas são um hash map cujo chave é a pergunta e o valor é a resposta
    private LinkedHashMap<String, String> perguntas;
    private Monster monstro;
    Random gerador = new Random();



    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description) 
    {
        this.description = description;
        atributo = Attribute.VAZIA;
        exits = new HashMap<>();
    }

    public Room(String description, Attribute atributo) 
    {
        this.description = description;
        this.atributo = atributo;
        ativaAtributo();
        
        exits = new HashMap<>();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Ativa o atributo relacionado a instância de uma sala.
     */
    private void ativaAtributo() {
        switch (atributo) {
            case MONSTER:
                monstro = new Monster();
                atributo_ativo = true;
                break;
            case CHEFE:
                monstro = new Monster(true);
                atributo_ativo = true;
                break;
            case QUIZ:
                perguntas = new LinkedHashMap<>();
                perguntas.put("Quanto vale um radiano?", "57.3");
                perguntas.put("Qual o numero maximo de nós que uma arvore binaria de altura 4 pode ter?", "31");
                perguntas.put("No meu jardim existe 3 pés de alface, 1 de pepino e 5 de cenoura. Quantos pés eu tenho no total?", "2");
                atributo_ativo = true;
            default:
                atributo_ativo = true;
                break;
        }
    }

    public void usaAtributo(Player jogador) {
        if (atributo_ativo) {
            switch(atributo) {
                case ARMADILHA:
                    jogador.sofre_dano(atributo.getValor_associado());
                    break;
                case CURA:
                    jogador.recebe_cura(atributo.getValor_associado());
                    atributo_ativo = false;
                    break;
                case CHEFE:
                case MONSTER:
                    if (monstro.estaVivo()) {
                            System.out.println(monstro.ataque(jogador));
                            System.out.println(jogador.printStats(monstro));
                        }
                    else 
                        atributo_ativo = false;
                    break;
                case QUIZ:
                    eruditaPergunta(jogador);
                    atributo_ativo = false;
                    break;
                default:
                    break;
            }
        }
        else
            System.out.println(atributo.getmensagemAtributoUtilizado());
    }

    private String eruditaPergunta(Player jogador) {
        String resultado = new String();
        // Sets são aleatórios
        for (String key : perguntas.keySet()) {
            Scanner leitor = new Scanner(System.in);
            System.out.println(key);
            String resposta = leitor.nextLine();
            leitor.close();
            if (resposta == perguntas.get(key))
                resultado = "Resposta correta! Você recebeu um feitiço de bola de fogo";
            else
                resultado = "Essa não é a resposta. A resposta correta é " + perguntas.get(key);
            break;
        }

        return resultado;
    }

    public void desativaAtributo() {
        atributo_ativo = false;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "Você está " + description + ".\n" + getExitString();
    }

    public String getAttributeDescription() {
        return atributo.getDescricao();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Saídas:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    public boolean atributoEstaAtivo() {
        return atributo_ativo;
    }

    public boolean temMonstro() {
        return atributo.equals(Attribute.MONSTER) || atributo.equals(Attribute.CHEFE);
    }

    /**
     * 
     * @return O objeto monstro, se a sala tiver um monstro. Caso contrário, null;
     */
    public Monster getMonstro() {
        if (temMonstro()){
            return monstro;
        }
        return null;
    }
    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
}