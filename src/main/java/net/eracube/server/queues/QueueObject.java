package net.eracube.server.queues;

public interface QueueObject {
    /**
     * If the player is alone, its name.
     * If the player is inside a party the leader.
     *
     * @return the name of the queue owner
     */
    String getQueueOwner();

    /**
     * It allows us to know which queue is.
     *
     * @return the queue id
     */
    String getQueueId();

    /**
     * The most power will have a better priority!
     * If this is a party, the leader's power.
     *
     * @return the power of the queue object
     */
    int getPower();

    /**
     * If a player is alone so 1 if he contains friends in
     * his party, more.
     *
     * @return the size of the queue.
     */
    int getQueueSize();
}
