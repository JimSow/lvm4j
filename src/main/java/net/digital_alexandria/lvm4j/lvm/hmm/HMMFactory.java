package net.digital_alexandria.lvm4j.lvm.hmm;

/**
 * @author Simon Dirmeier {@literal simon.dirmeier@gmx.de}
 */
public class HMMFactory
{

    private static HMMFactory _factory;

    private HMMFactory(){}

    public static HMMFactory instance()
    {
        if (_factory == null)
            _factory = new HMMFactory();
        return _factory;
    }

    /**
     * Create a HMM using the provided file. The HMM can be used for training and prediction.
     * If the edge weights are binary training has to be done at first.
     *
     * @param hmmFile the file containing edge/node information
     * @return an HMM
     */
    public HMM hmm(String hmmFile)
    {
        return new HMM(hmmFile);
    }
}
