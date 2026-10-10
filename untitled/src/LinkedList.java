public class LinkedList {
    Node head=null;
    public void addNode(int value)
    {
        Node node=new Node(value);
        if(head==null)
        {
            head=node;
        }
        else{
          Node temp=head;
          while(temp.next!=null)
          {
              temp=temp.next;
          }
          temp.next=node;

        }
    }

    public void traverse()
    {
        Node temp=head;
        while(temp!=null)
        {
            System.out.println(temp.value);
            temp=temp.next;
        }
    }

    public void reverse()
    {
        //2->3>-5-8-9
        //9->8->5->3->2->null
        Node temp=head;
        Node temp1=null;
        while(temp!=null)
        {
            Node temp3=temp.next;
            temp.next=temp1;
            temp1=temp;
            temp=temp3;
        }
        //

    }
}
