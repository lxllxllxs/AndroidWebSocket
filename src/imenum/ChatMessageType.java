package imenum;

 public enum ChatMessageType{
        TextMessage,ImageMessage,RedPocketMessage;

        public static  boolean contain(String tyep){
            for (ChatMessageType messageType:ChatMessageType.values()){
                if (messageType.toString().equals(tyep)){
                    return true;
                }
            }
            return false;
        }

    public static String getName(){
        return "ChatMessageType";
    }
}