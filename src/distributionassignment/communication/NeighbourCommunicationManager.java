package distributionassignment.communication;

import distributionassignment.support.CommonSupport;
import distributionassignment.support.FileDetail;
import distributionassignment.support.NeighbourNode;
import distributionassignment.support.Node;

import java.util.ArrayList;

/**
 * Created by vidudaya on 4/11/16.
 */
public class NeighbourCommunicationManager {

    private final String JOIN = "JOIN";
    private final String JOINOK = "JOINOK";
    private final String LEAVE = "LEAVE";
    private final String LEAVEOK = "LEAVEOK";
    private final String SEARCH = "SER";
    private final String SEARCHOK = "SEROK";
    private final String BLANK = " ";
    private final String RATE = "RATE";
    private final String RATEOK = "RATEOK";
    private final String COMMENT = "COMM";
    private final String COMMENTOK = "COMMOK";
    private Sender sender;
    private CommonSupport commonSupport;

    public NeighbourCommunicationManager(Sender sender) {
        this.sender = sender;
        this.commonSupport = new CommonSupport();
    }

    public void searchFileInNetwork(String fileName, Node distributorNode) {
        System.out.println(distributorNode.getShell()
                .concat("Searching in network..."));

        String uniqueId = commonSupport.getUniqueId();
        //length SER IP port name file_name hops req_id
        String messageToSend = commonSupport.generateMessageToSend(SEARCH, distributorNode.getIp()
                , String.valueOf(distributorNode.getPort())
                , distributorNode.getNodeIdentifier()
                , fileName
                , "1"
                , uniqueId);

        distributorNode.getRequestCache().addToCache(uniqueId);

        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
        }
    }
    
    public void rateFileInNetwork(String fileName, String fileRate, Node distributorNode) {
        System.out.println(distributorNode.getShell()
                .concat("Rating in the network..."));

        String uniqueId = commonSupport.getUniqueId();
        //length SER IP port name file_name hops req_id
        String messageToSend = commonSupport.generateMessageToSend(RATE, distributorNode.getIp()
                , String.valueOf(distributorNode.getPort())
                , distributorNode.getNodeIdentifier()
                , fileName
                , fileRate
                , "1"
                , uniqueId);

        distributorNode.getRequestCache().addToCache(uniqueId);

        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
        }
    }

    public void commentFileInNetwork(String fileName, String comment, Node distributorNode) {
        System.out.println(distributorNode.getShell()
                .concat("Rating in the network..."));

        String uniqueId = commonSupport.getUniqueId();
        //length SER IP port name file_name hops req_id
        String messageToSend = commonSupport.generateMessageToSend(COMMENT, distributorNode.getIp()
                , String.valueOf(distributorNode.getPort())
                , distributorNode.getNodeIdentifier()
                , fileName
                , comment
                , "1"
                , uniqueId);

        distributorNode.getRequestCache().addToCache(uniqueId);

        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
        }
    }
    
    public void joinWithInitialNeighbours(ArrayList<NeighbourNode> selectedNodes, Node distributorNode) {
        for (NeighbourNode node : selectedNodes) {
            joinWithNeighbour(node, distributorNode);
            ArrayList<FileDetail> fileList = distributorNode.getTextStore().getFiles();
            for (FileDetail file : fileList) {
                if(file.getFileRate() != -1) {
                    rateFileInNetwork(file.getFileName().replace(" ", "_"), String.valueOf(file.getFileRate()), distributorNode);
                }
                if(file.getCommentList().isEmpty() != true) {
                    for (String comment : file.getCommentList()) {
                        commentFileInNetwork(file.getFileName().replace(" ", "_"), comment, distributorNode);
                    }
                }
            }
        }
    }

    public void leaveFromNetwork(ArrayList<NeighbourNode> neighbours, Node distributorNode) {
        for (NeighbourNode neighbour : neighbours) {
            leaveNeighbour(neighbour, distributorNode);
            System.out.println(distributorNode.getShell()
                    .concat("LEAVE message send to " + neighbour.getNodeIdentifier()));
        }
    }

    public void leaveNeighbour(NeighbourNode neighbour, Node node) {
        //length LEAVE IP_address port_no name
        String messageToSend = commonSupport.generateMessageToSend(LEAVE, node.getIp()
                , String.valueOf(node.getPort()), node.getNodeIdentifier());
        sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
    }

    public void joinWithNeighbour(NeighbourNode neighbour, Node node) {
        //length JOIN IP_address port_no name
        String message = JOIN.concat(BLANK).concat(node.getIp()).concat(BLANK).concat(String.valueOf(node.getPort()))
                .concat(BLANK).concat(node.getNodeIdentifier());
        int length = message.length() + 5;
        String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
        sender.sendMessage(messageToSend, neighbour.getIp(), neighbour.getPort());
    }

    public void forwardSearchMessageToRoutingTable(String msg, Node distributorNode) {
        for (NeighbourNode neighbour : distributorNode.getRoutingTable().getAllNeighbours()) {
            sender.sendMessage(msg, neighbour.getIp(), neighbour.getPort());
        }
    }

    public void processReceivedMessage(String msg, Node distributorNode) {
        //length JOIN IP_address port_no name
        //length JOINOK value
        String tokens[] = msg.trim().split(" ");
        String cmd = tokens[1].trim();

        if (JOIN.equals(cmd)) {
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();

            try {
                Integer portNum = Integer.parseInt(port);
                NeighbourNode newNode = new NeighbourNode(ip, portNum, name);
                boolean success = distributorNode.getRoutingTable().addToRoutingTable(newNode);

                if (success) {
                    // send JOINOK with 0
                    String message = JOINOK.concat(BLANK).concat("0");
                    int length = message.length() + 5;
                    String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
                    sender.sendMessage(messageToSend, newNode.getIp(), newNode.getPort());
                    ArrayList<FileDetail> fileList = distributorNode.getTextStore().getFiles();
                    for (FileDetail file : fileList) {
                        if(file.getFileRate() != -1) {
                            rateFileInNetwork(file.getFileName().replace(" ", "_"), String.valueOf(file.getFileRate()), distributorNode);
                        }
                        if(file.getCommentList().isEmpty() != true) {
                            for (String comment : file.getCommentList()) {
                                commentFileInNetwork(file.getFileName().replace(" ", "_"), comment, distributorNode);
                            }
                        }
                    }
                } else {
                    //send JOINOK with 9999
                    String message = JOINOK.concat(BLANK).concat("9999");
                    int length = message.length() + 5;
                    String messageToSend = commonSupport.getFormattedNumber(length, 4).concat(BLANK).concat(message);
                    sender.sendMessage(messageToSend, newNode.getIp(), newNode.getPort());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (LEAVE.equals(cmd)) {
            //length LEAVE IP_address port_no name
            //length LEAVEOK value
            //0028 LEAVE 64.12.123.190 432 name
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();

            try {
                Integer portNum = Integer.parseInt(port);
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                boolean success = distributorNode.getRoutingTable().removeFromRoutingTable(node);

                String messageToSend = "";
                if (success) {
                    System.out.println(distributorNode.getShell()
                            .concat("[ Node " + name + " left the network ]"));
                    messageToSend = commonSupport.generateMessageToSend(LEAVEOK, "0");
                } else {
                    messageToSend = commonSupport.generateMessageToSend(LEAVEOK, "9999");
                }
                sender.sendMessage(messageToSend, node.getIp(), node.getPort());

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (JOINOK.equals(cmd)) {
            if (distributorNode.isDebugMode()) {
                if ("0".equals(tokens[2])) {
                    System.out.println(distributorNode.getShell()
                            .concat("Join Success"));
                } else if ("9999".equals(tokens[2])) {
                    System.out.println("Join Failed");
                } else {
                    System.out.println("Bad JOINOK response");
                }
            }
        } else if (LEAVEOK.equals(cmd)) {
            if (distributorNode.isDebugMode()) {
                if ("0".equals(tokens[2])) {
                    System.out.println(distributorNode.getShell()
                            .concat("LEAVE Success with a Node"));
                } else if ("9999".equals(tokens[2])) {
                    System.out.println("LEAVE Failed with a Node");
                } else {
                    System.out.println("Bad LEAVEOK response");
                }
            }
        } else if (SEARCH.equals(cmd)) {
            //length SER IP port name file_name hops req_id
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String fileName = tokens[5].trim();
            String hops = tokens[6].trim();
            String reqId = tokens[7].trim();
            int hopsCount = 0;
            int portNum = 0;
            try {
                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (distributorNode.isDebugMode()) {
                System.out.println("Search request for [ " + fileName + " ] from " + name);
            }
            boolean isDuplicateReq = distributorNode.getRequestCache().isPossibleDuplicate(reqId);
            if (!isDuplicateReq) {
                distributorNode.getRequestCache().addToCache(reqId);
                ArrayList<FileDetail> localStore = distributorNode.getTextStore().returnAllPartialMatches(fileName);

                try {
                    if (localStore.size() > 0) {
                        //length SEROK no_files IP port name hops filename1 filename2
                        String messageToSend = commonSupport.generateMessageToSend(SEARCHOK
                                , String.valueOf(localStore.size())
                                , distributorNode.getIp()
                                , String.valueOf(distributorNode.getPort())
                                , distributorNode.getNodeIdentifier()
                                , String.valueOf(hopsCount)
                                , commonSupport.getCombinedStringOfList(localStore));

                        if (distributorNode.isDebugMode()) {
                            System.out.println("Local Search OK : [ " + commonSupport.getCombinedStringOfList(localStore) + " ]");
                        }
                        sender.sendMessage(messageToSend, ip, portNum);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("No local results found");
                        }
                    }

                    if (hopsCount < 10) {
                        // Forward the request to RT
                        // we need to forward the request as flooding, then without anonymity forward the result
                        String messageToSend = commonSupport.generateMessageToSend(SEARCH
                                , ip
                                , port
                                , name
                                , fileName
                                , String.valueOf(hopsCount)
                                , reqId);
                        forwardSearchMessageToRoutingTable(messageToSend, distributorNode);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("Maximum hops count reached - message dropped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                if (distributorNode.isDebugMode()) {
                    System.out.println("Duplicate Request");
                }
                // duplicate request - avoid processing
            }
        } else if (SEARCHOK.equals(cmd)) {
            String count = tokens[2].trim();
            String ip = tokens[3].trim();
            String port = tokens[4].trim();
            String name = tokens[5].trim();
            String hops = tokens[6].trim();
            int countOfFiles = 0;
            int hopsCount = 0;
            int portNum = 0;
            int pointer = 7;

            try {
                countOfFiles = Integer.parseInt(count);
                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);

                // add the responder to the routing table
                // RT can get larger by this, since non-neighbours can be added also
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                distributorNode.getRoutingTable().addToRoutingTable(node);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.println(("\n").concat(distributorNode.getShell())
                    .concat("[ Total of " + countOfFiles + " matching files found in " + name + " ]"));

            if (countOfFiles > 0) {
                for (int i = 0; i < countOfFiles; ++i) {
                    System.out.println("\t\t" + tokens[pointer + i].replaceAll("_", " "));
                }
            }

            System.out.print(distributorNode.getShell());
        } else if(RATE.equals(cmd)) {
            //length RATE IP port name file_name file_rate hops req_id
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String fileName = tokens[5].trim().replace("_", " ");
            String rateStr = tokens[6].trim();
            double fileRate = 0;
            String hops = tokens[7].trim();
            String reqId = tokens[8].trim();
            int hopsCount = 0;
            int portNum = 0;
            try {
                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);
                fileRate = Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (distributorNode.isDebugMode()) {
                System.out.println("Search request for [ " + fileName + " ] from " + name);
            }
            boolean isDuplicateReq = distributorNode.getRequestCache().isPossibleDuplicate(reqId);
            if (!isDuplicateReq) {
                distributorNode.getRequestCache().addToCache(reqId);
                ArrayList<FileDetail> localStore = distributorNode.getTextStore().returnAllPartialMatches(fileName);

                try {
                    if (localStore.size() > 0) {
                        
                        for (FileDetail file : localStore) {
                            file.setFileRate(fileRate);
                        }
                        //length RATEOK fileName rate IP port name hops filename1 filename2
                        String messageToSend = commonSupport.generateMessageToSend(RATEOK
                                , fileName.replace(" ", "_")
                                , String.valueOf(localStore.get(0).getFileRate())
                                , distributorNode.getIp()
                                , String.valueOf(distributorNode.getPort())
                                , distributorNode.getNodeIdentifier()
                                , String.valueOf(hopsCount)
                                , commonSupport.getCombinedStringOfList(localStore));

                        if (distributorNode.isDebugMode()) {
                            System.out.println("Local Rate OK : [ " + commonSupport.getCombinedStringOfList(localStore) + " Rate " + String.valueOf(localStore.get(0).getFileRate()) + " ]");
                        }
                        sender.sendMessage(messageToSend, ip, portNum);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("No local results found");
                        }
                    }

                    if (hopsCount < 10) {
                        // Forward the request to RT
                        // we need to forward the request as flooding, then without anonymity forward the result
                        String messageToSend = commonSupport.generateMessageToSend(RATE
                                , ip
                                , port
                                , name
                                , fileName.replace(" ", "_")
                                , rateStr
                                , String.valueOf(hopsCount)
                                , reqId);
                        forwardSearchMessageToRoutingTable(messageToSend, distributorNode);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("Maximum hops count reached - message dropped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(RATEOK.equals(cmd)) {
            String fileName = tokens[2].trim().replace("_", " ");
            String filerate = tokens[3].trim();
            String ip = tokens[4].trim();
            String port = tokens[5].trim();
            String name = tokens[6].trim();
            int portNum = 0;

            try {
                portNum = Integer.parseInt(port);

                // add the responder to the routing table
                // RT can get larger by this, since non-neighbours can be added also
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                distributorNode.getRoutingTable().addToRoutingTable(node);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.println(("\n").concat(distributorNode.getShell())
                    .concat("[ File name " + fileName + " rate added " + filerate + " ]"));
            System.out.print(distributorNode.getShell());
        } else if(COMMENT.equals(cmd)) {
            //length RATE IP port name file_name file_rate hops req_id
            String ip = tokens[2].trim();
            String port = tokens[3].trim();
            String name = tokens[4].trim();
            String fileName = tokens[5].trim().replace("_", " ");
            String comment = tokens[6].trim();
            String hops = tokens[7].trim();
            String reqId = tokens[8].trim();
            int hopsCount = 0;
            int portNum = 0;
            try {
                hopsCount = Integer.parseInt(hops) + 1;
                portNum = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (distributorNode.isDebugMode()) {
                System.out.println("Search request for [ " + fileName + " ] from " + name);
            }
            boolean isDuplicateReq = distributorNode.getRequestCache().isPossibleDuplicate(reqId);
            if (!isDuplicateReq) {
                distributorNode.getRequestCache().addToCache(reqId);
                ArrayList<FileDetail> localStore = distributorNode.getTextStore().returnAllPartialMatches(fileName);

                try {
                    if (localStore.size() > 0) {
                        
                        for (FileDetail file : localStore) {
                            file.addComment(comment);
                        }
                        //length RATEOK fileName rate IP port name hops filename1 filename2
                        String messageToSend = commonSupport.generateMessageToSend(COMMENTOK
                                , fileName.replace(" ", "_")
                                , String.valueOf(localStore.get(0).getFileRate())
                                , distributorNode.getIp()
                                , String.valueOf(distributorNode.getPort())
                                , distributorNode.getNodeIdentifier()
                                , String.valueOf(hopsCount)
                                , commonSupport.getCombinedStringOfList(localStore));

                        if (distributorNode.isDebugMode()) {
                            System.out.println("Local Rate OK : [ " + commonSupport.getCombinedStringOfList(localStore) + " comment " + comment + " ]");
                        }
                        sender.sendMessage(messageToSend, ip, portNum);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("No local results found");
                        }
                    }

                    if (hopsCount < 10) {
                        // Forward the request to RT
                        // we need to forward the request as flooding, then without anonymity forward the result
                        String messageToSend = commonSupport.generateMessageToSend(COMMENT
                                , ip
                                , port
                                , name
                                , fileName.replace(" ", "_")
                                , comment
                                , String.valueOf(hopsCount)
                                , reqId);
                        forwardSearchMessageToRoutingTable(messageToSend, distributorNode);
                    } else {
                        if (distributorNode.isDebugMode()) {
                            System.out.println("Maximum hops count reached - message dropped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 
        } else if(COMMENTOK.equals(cmd)) {
            String fileName = tokens[2].trim().replace("_", " ");
            String filerate = tokens[3].trim();
            String ip = tokens[4].trim();
            String port = tokens[5].trim();
            String name = tokens[6].trim();
            int portNum = 0;

            try {
                portNum = Integer.parseInt(port);

                // add the responder to the routing table
                // RT can get larger by this, since non-neighbours can be added also
                NeighbourNode node = new NeighbourNode(ip, portNum, name);
                distributorNode.getRoutingTable().addToRoutingTable(node);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            System.out.println(("\n").concat(distributorNode.getShell())
                    .concat("[ File name " + fileName + " comment added " + filerate + " ]"));
            System.out.print(distributorNode.getShell());
        }
    }
}
