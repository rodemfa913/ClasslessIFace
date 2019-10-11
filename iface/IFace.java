package iface;

import java.util.Scanner;

public class IFace {
    static boolean[][] friendRequests, friendship;
    static Scanner input;
    static String[] names, passwords, users;
    static String[][][] profiles;

    public static void main(String[] args) {
        friendRequests = new boolean[16][16]; // [user][friend]
        friendship = new boolean[16][16]; // [friend1][friend2]
        input = new Scanner(System.in);
        names = new String[16];
        passwords = new String[16];
        profiles = new String[16][8][2]; // [profile][attribute][key/value]
        users = new String[16];

        String[] startActions = {"criar conta", "entrar"/*, "remover conta",
            "enviar mensagem", "criar comunidade", "entrar em comunidade",
            "adicionar membro", "visualizar informações"*/
        };

        while (true) {
            System.out.println("---\n0 - fechar");
            int a;
            for (a = 1; a <= startActions.length; a++) {
                System.out.println(a + " - " + startActions[a - 1]);
            }
            System.out.print("---\nEscolha uma ação: ");
            a = input.nextInt(); input.nextLine();

            if (a == 0) break;

            switch (a) {
                case -1:
                debug();
                break;

                case 1:
                signUp();
                break;

                case 2:
                signIn();
                break;

                default:
                System.out.println("<Erro> Opção inválida.");
            }
        }
    }

    static void acceptFriend(int user) {
        boolean[] friendRequests = IFace.friendRequests[user];
        boolean noRequest = true;

        for (int friend = 0; friend < friendRequests.length; friend++) {
            if (!friendRequests[friend]) continue;
            else friendRequests[friend] = noRequest = false;

            String name = names[friend];

            System.out.println("Usuário: " + name);
            System.out.print("Aceitar? (s/n): ");
            if (input.nextLine().equals("s")) {
                friendship[user][friend] = friendship[friend][user] = true;
                System.out.println("Amigo " + name + " adicionado.");
            }
        }

        if (noRequest) System.out.println("<Aviso> Nenhuma solicitação de amizade.");
    }

    static void addFriend(int user) {
        int friend = getUser("Login do amigo: ");
        if (friend < 0) {
            System.out.println("<Erro> Usuário não encontrado.");
            return;
        }
        friendRequests[friend][user] = true;

        System.out.println("Solicitação de amizade enviada para " + names[friend] + ".");
    }

    static void debug() {
        System.out.println("Usuários:\n---");
        for (int user = 0; user < users.length; user++) {
            String login = users[user];
            if (login == null) continue;

            System.out.println(login);
            System.out.println("senha: " + passwords[user]);
            System.out.println("nome: " + names[user]);

            String[][] profile = profiles[user];
            for (String[] attribute : profile) {
                String key = attribute[0];
                if (key == null) continue;
                String value = attribute[1];
                System.out.println(key + ": " + value);
            }

            boolean[] friends = friendship[user];
            System.out.println("amigos:");
            for (int friend = 0; friend < friends.length; friend++)
                if (friends[friend]) System.out.println("  " + names[friend]);
        }
    }
    /*
    private static void debug() {

      System.out.println("Comunidades:\n---");

      for (String name : communities.keySet()) {

         System.out.println(communities.get(name));

      }

   }
    */

    static void editProfile(int user) {
        String[][] profile = profiles[user];
        while (true) {
            System.out.print("Atributo ('-' para encerrar): ");
            String key = input.nextLine().toLowerCase();
            if (key.isEmpty() || key.equals("-")) break;

            System.out.print("Valor: ");
            String value = input.nextLine();

            if (key.equals("nome")) names[user] = value;
            else {
                String[] attribute = null;

                for (String[] a : profile) {
                    String k = a[0];
                    if (k == null) {
                        if (attribute == null) attribute = a;
                    } else if (k.equals(key)) {
                        attribute = a;
                        break;
                    }
                }

                if (attribute != null) {
                    attribute[0] = key;
                    attribute[1] = value;
                }
            }
        }
    }

    static int getUser(String prompt) {
        System.out.print(prompt);
        String login = input.nextLine();

        int user = -1;
        for (int u = 0; u < users.length; u++) {
            String lg = users[u];
            if (lg != null && lg.equals(login)) {
                user = u;
                break;
            }
        }

        return user;
    }

    static void signIn() {
        int user = getUser("Login: ");
        if (user < 0) {
            System.out.println("<Erro> Usuário não encontrado.");
            return;
        }

        System.out.print("Senha: ");
        String password = input.nextLine();
        if (!passwords[user].equals(password)) {
            System.out.println("<Erro> Senha incorreta.");
            return;
        }

        String[] actions = {"editar perfil", "adicionar amigo", "aceitar amigo"};

        while (true) {
            System.out.println("---\n0 - sair");
            int a;
            for (a = 1; a <= actions.length; a++) {
                System.out.println(a + " - " + actions[a - 1]);
            }
            System.out.print("---\nEscolha uma ação: ");
            a = input.nextInt(); input.nextLine();

            if (a == 0) break;

            switch (a) {
                case 1:
                editProfile(user);
                break;

                case 2:
                addFriend(user);
                break;

                case 3:
                acceptFriend(user);
                break;

                default:
                System.out.println("<Erro> Opção inválida.");
            }
        }
    }

    static void signUp() {
        System.out.print("Login: ");
        String login = input.nextLine();
        if (login.isEmpty()) login = "0";

        int user = -1;
        for (int u = 0; u < users.length; u++) {
            String lg = users[u];
            if (lg == null) {
                if (user < 0) user = u;
            } else if (lg.equals(login)) {
                System.out.println("<Erro> Login já existente.");
                return;
            }
        }

        users[user] = login;

        System.out.print("Senha: ");
        passwords[user] = input.nextLine();

        System.out.print("Nome de usuário: ");
        String name = input.nextLine();
        names[user] = name;

        System.out.println("Conta de usuário " + name + " criada.");
    }
}
