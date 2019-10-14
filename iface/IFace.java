package iface;

import java.util.Scanner;

public class IFace {
    static String[][] communities, users;
    static boolean[][] friendRequests, friendship, memberRequests, members;
    static Scanner input;
    static String[][][] messages, profiles;
    static int nAttribute, nUser, nCommunity, nMessage;
    static int[] owners;

    public static void main(String[] args) {
        nAttribute = nMessage = 8;
        nCommunity = nUser = 16;

        communities = new String[nCommunity][2]; // [community][name/description]
        friendRequests = new boolean[nUser][nUser]; // [user][friend]
        friendship = new boolean[nUser][nUser]; // [friend1][friend2]
        input = new Scanner(System.in);
        memberRequests = new boolean[nCommunity][nUser]; // [community][member]
        members = new boolean[nCommunity][nUser]; // [community][member]
        messages = new String[nUser][nUser][nMessage]; // [sender][receiver][message]

        owners = new int[nCommunity];
        for (int owner = 0; owner < nUser; owner++)
            owners[owner] = -1;

        profiles = new String[nUser][nAttribute][2]; // [user][attribute][key/value]
        users = new String[nUser][3]; // [user][login/password/name]

        while (true) {
            System.out.println("---\n0 - fechar\n1 - criar conta\n2 - entrar\n---");
            System.out.print("Escolha uma ação: ");
            int a = input.nextInt(); input.nextLine();

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

        for (int friend = 0; friend < nUser; friend++) {
            if (!friendRequests[friend]) continue;
            else
                friendRequests[friend] = noRequest = false;

            String name = users[friend][2];

            System.out.println("Usuário: " + name);
            System.out.print("Aceitar? (s/n): ");
            if (input.nextLine().equals("s")) {
                friendship[user][friend] = friendship[friend][user] = true;
                System.out.println("Amigo " + name + " adicionado.");
            }
        }

        if (noRequest)
            System.out.println("<Aviso> Nenhuma solicitação de amizade.");
    }

    static void acceptMember(int owner) {
        boolean noRequest = true;

        for (int community = 0; community < nCommunity; community++) {
            if (owners[community] != owner) continue;

            String communityName = communities[community][0];
            boolean[] memberRequests = IFace.memberRequests[community];

            for (int member = 0; member < nUser; member++) {
                if (!memberRequests[member]) continue;
                else
                    memberRequests[member] = noRequest = false;

                String memberName = users[member][2];

                System.out.println("Comunidade: " + communityName);
                System.out.println("Membro: " + memberName);
                System.out.print("Aceitar? (s/n): ");
                if (input.nextLine().equals("s")) {
                    members[community][member] = true;
                    System.out.println("Membro " + memberName + " adicionado.");
                }
            }
        }

        if (noRequest)
            System.out.println("<Aviso> Nenhuma solicitação de participação.");
    }

    static void addCommunity(int owner) {
        System.out.print("Nome da comunidade: ");
        String name = input.nextLine();
        if (name.isEmpty())
            name = "0";

        int community = -1;
        for (int c = 0; c < nCommunity; c++) {
            String nm = communities[c][0];
            if (nm == null) {
                if (community < 0) community = c;
            } else if (nm.equals(name)) {
                System.out.println("<Erro> comunidade já existente.");
                return;
            }
        }

        communities[community][0] = name;
        owners[community] = owner;
        members[community][owner] = true;

        System.out.print("Descrição: ");
        communities[community][1] = input.nextLine();

        System.out.println("Comunidade " + name + " criada.");
    }

    static void addFriend(int user) {
        int friend = getUser("Login do amigo: ");
        if (friend < 0) {
            System.out.println("<Erro> Usuário não encontrado.");
            return;
        }
        friendRequests[friend][user] = true;

        System.out.println("Solicitação de amizade enviada para " + users[friend][2] + ".");
    }

    static void debug() {
        System.out.println("Usuários:");
        for (int user = 0; user < nUser; user++) {
            String login = users[user][0];
            if (login == null) continue;

            System.out.println("---\nLogin: " + login);
            System.out.println("Senha: " + users[user][1]);
            System.out.println("Nome: " + users[user][2]);
        }

        System.out.println("---\nComunidades:");
        for (int community = 0; community < nCommunity; community++) {
            String name = communities[community][0];
            if (name == null) continue;

            System.out.println("---\nNome: " + name);
            System.out.println("Proprietário: " + users[owners[community]][2]);

            boolean[] members = IFace.members[community];
            System.out.println("Membros:");
            for (int member = 0; member < nUser; member++)
                if (members[member])
                    System.out.println("  " + users[member][2]);
        }
    }

    static void editProfile(int user) {
        String[][] profile = profiles[user];
        while (true) {
            System.out.print("Atributo ('-' para encerrar): ");
            String key = input.nextLine();
            key = key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
            if (key.isEmpty() || key.equals("-")) break;

            System.out.print("Valor: ");
            String value = input.nextLine();

            if (key.equals("Nome"))
                users[user][2] = value;
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

    static void enterCommunity(int member) {
        int community = getCommunity();
        if (community < 0) {
            System.out.println("<Erro> Comunidade não encontrada.");
            return;
        }

        memberRequests[community][member] = true;

        System.out.println(
            "Solicitação de participação enviada para " + communities[community][0]
        );
    }

    static int getCommunity() {
        System.out.print("Nome da comunidade: ");
        String name = input.nextLine();

        int community = -1;
        for (int c = 0; c < nCommunity; c++) {
            String nm = communities[c][0];
            if (nm != null && nm.equals(name)) {
                community = c;
                break;
            }
        }

        return community;
    }

    static int getUser(String prompt) {
        System.out.print(prompt);
        String login = input.nextLine();

        int user = -1;
        for (int u = 0; u < nUser; u++) {
            String lg = users[u][0];
            if (lg != null && lg.equals(login)) {
                user = u;
                break;
            }
        }

        return user;
    }

    static void sendMessage(int sender) {
        System.out.println("---\n0 - usuário\n1 - comunidade\n---");
        System.out.print("Enviar mensagem para: ");
        int r = input.nextInt(); input.nextLine();

        boolean[] receivers;
        String recName;
        if (r == 1) {
            int receiver = getCommunity();
            if (receiver < 0) {
                System.out.println("<Erro> Comunidade não encontrada.");
                return;
            }
            if (!members[receiver][sender]) {
                System.out.println("<Erro> É necessário ser membro da comunidade.");
                return;
            }
            receivers = members[receiver];
            recName = communities[receiver][0];
        } else {
            int receiver = getUser("Login do destinatário: ");
            if (receiver < 0) {
                System.out.println("<Erro> Usuário não encontrado.");
                return;
            }
            receivers = new boolean[nUser];
            receivers[receiver] = true;
            recName = users[receiver][2];
        }

        System.out.println("Conteúdo da mensagem ('.' para encerrar):");
        String message = "";
        while (true) {
            String line = input.nextLine();
            if (line.isEmpty() || line.equals(".")) break;
            message += line + "\n";
        }

        for (int receiver = 0; receiver < nUser; receiver++) {
            if (!receivers[receiver] || receiver == sender) continue;

            for (int m = 0; m < nMessage; m++)
                if (messages[sender][receiver][m] == null) {
                    messages[sender][receiver][m] = message;
                    break;
                }
        }

        System.out.println("Mensagem enviada para " + recName + ".");
    }

    static void signIn() {
        int user = getUser("Login: ");
        if (user < 0) {
            System.out.println("<Erro> Usuário não encontrado.");
            return;
        }

        System.out.print("Senha: ");
        String password = input.nextLine();
        if (!users[user][1].equals(password)) {
            System.out.println("<Erro> Senha incorreta.");
            return;
        }

        String[] actions = {
            "sair", "editar perfil", "adicionar amigo", "aceitar amigo",
            "criar comunidade", "entrar em comunidade", "aceitar membro",
            "enviar mensagem", "visualizar informações", "remover conta"
        };

        while (true) {
            System.out.println("---");
            int a;
            for (a = 0; a < actions.length; a++) {
                System.out.println(a + " - " + actions[a]);
            }
            System.out.print("---\nEscolha uma ação: ");
            a = input.nextInt(); input.nextLine();

            if (a == 0) break;
            if (a == 9) {
                signOut(user);
                break;
            }

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

                case 4:
                addCommunity(user);
                break;

                case 5:
                enterCommunity(user);
                break;

                case 6:
                acceptMember(user);
                break;

                case 7:
                sendMessage(user);
                break;

                case 8:
                viewInfo(user);
                break;

                default:
                System.out.println("<Erro> Opção inválida.");
            }
        }
    }

    static void signOut(int user) {
        for (int community = 0; community < nCommunity; community++) {
            if (owners[community] == user) {
                for (int member = 0; member < nUser; member++) {
                    members[community][member] = false;
                    memberRequests[community][member] = false;
                }
                owners[community] = -1;
                communities[community][0] = communities[community][1] = null;
            }
            members[community][user] = false;
            memberRequests[community][user] = false;
        }

        for (int friend = 0; friend < nUser; friend++) {
            friendship[user][friend] = friendship[friend][user] = false;
            friendRequests[user][friend] = friendRequests[friend][user] = false;
        }

        for (int sr = 0; sr < nUser; sr++)
            for (int message = 0; message < nMessage; message++)
                messages[user][sr][message] = messages[sr][user][message] = null;

        for (int attribute = 0; attribute < nAttribute; attribute++)
            profiles[user][attribute][0] = profiles[user][attribute][1] = null;

        String name = users[user][2];
        users[user][0] = users[user][1] = users[user][2] = null;

        System.out.println("Conta de usuário " + name + " removida.");
    }

    static void signUp() {
        System.out.print("Login: ");
        String login = input.nextLine();
        if (login.isEmpty()) login = "0";

        int user = -1;
        for (int u = 0; u < nUser; u++) {
            String lg = users[u][0];
            if (lg == null) {
                if (user < 0) user = u;
            } else if (lg.equals(login)) {
                System.out.println("<Erro> Login já existente.");
                return;
            }
        }

        users[user][0] = login;

        System.out.print("Senha: ");
        users[user][1] = input.nextLine();

        System.out.print("Nome de usuário: ");
        String name = input.nextLine();
        users[user][2] = name;

        System.out.println("Conta de usuário " + name + " criada.");
    }

    static void viewInfo(int user) {
        System.out.println("---\n0 - perfil\n1 - comunidades\n2 - amigos\n3 - mensagens\n---");
        System.out.print("Informação: ");
        int info = input.nextInt(); input.nextLine();

        switch (info) {
            case 1:
            System.out.println("Comunidades:");
            for (int community = 0; community < nCommunity; community++) {
                if (!members[community][user]) continue;

                System.out.println("---\nNome: " + communities[community][0]);
                System.out.println("Descrição: " + communities[community][1]);
                System.out.println("Proprietário: " + users[owners[community]][2]);
            }
            break;

            case 2:
            System.out.println("Amigos:");
            for (int friend = 0; friend < nUser; friend++)
                if (friendship[user][friend])
                    System.out.println("  " + users[friend][2]);
            break;

            case 3:
            System.out.println("Mensagens:");
            for (int sender = 0; sender < nUser; sender++) {
                for (int m = 0; m < nMessage; m++) {
                    String message = messages[sender][user][m];
                    if (message != null) {
                        System.out.println("---\nRemetente: " + users[sender][2]);
                        System.out.println(message);
                    }
                    messages[sender][user][m] = null; // read => delete
                }
            }
            break;

            default:
            System.out.println("Nome: " + users[user][2]);
            String[][] profile = profiles[user];
            for (String[] attribute : profile) {
                String key = attribute[0];
                if (key == null) continue;
                String value = attribute[1];
                System.out.println(key + ": " + value);
            }
        }
    }
}