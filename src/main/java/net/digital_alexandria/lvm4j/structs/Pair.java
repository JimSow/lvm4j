package net.digital_alexandria.lvm4j.structs;

/**
 * @author Simon Dirmeier {@literal simon.dirmeier@gmx.de}
 */
public class Pair<T, U>
{
    private final T t;
    private final U u;

    public Pair(T t, U u)
    {
        this.t = t;
        this.u = u;
    }

    public T getFirst()
    {
        return t;
    }

    public U getSecond()
    {
        return u;
    }

}
