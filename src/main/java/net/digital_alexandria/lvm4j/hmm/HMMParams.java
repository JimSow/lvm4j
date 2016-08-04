package net.digital_alexandria.lvm4j.hmm;

import net.digital_alexandria.lvm4j.structs.Pair;
import net.digital_alexandria.lvm4j.structs.Triple;

import java.util.ArrayList;
import java.util.List;

/**
 * HMMParam class stores several attributes a HMM must have.
 *
 * @author Simon Dirmeier {@literal simon.dirmeier@gmx.de}
 */
 public final class HMMParams
{
    // the possible latent variables of an HMM as characters (i.e. discrete space)
    private char[] _states;
    // the possible observed variables of an HMM as characters (i.e. discrete space)
    private char[] _observations;
    // n-th order of the markov chain
    private int _order;
    // is the provided HMM file trained or not
    private boolean _isTrainingParam;
    // weightings in case the HMM is trained
    private List<Pair<String, Double>> _startWeights;
    // transition probabilities in case the HMM is trained
    private List<Triple<String, String, Double> > _transitionWeights;
    // emission probabilities in case the HMM is trained
    private List<Triple<String, String, Double>> _emissionWeights;

    /**
     * Get a new instance of an HMMParams object.
     *
     * @return returns an HMMParams object
     */
    public static HMMParams newInstance() { return new HMMParams(); }

    private HMMParams()
    {
        this._isTrainingParam = false;
        this._startWeights = new ArrayList<>();
        this._transitionWeights = new ArrayList<>();
        this._emissionWeights = new ArrayList<>();
    }

    public final int order() { return _order; }

    public final char[] observations() { return _observations; }

    public final char[] states() { return _states; }

    public final void observations(char[] observations)
    {
        this._observations = observations;
    }

    public final void order(int order) { this._order = order; }

    public final void states(char[] states) { this._states = states; }

    public final List<Pair<String, Double>> startProbabilities()
    {
        return _startWeights;
    }

    public final List<Triple<String, String, Double>> transitionProbabilities()
    {
        return _transitionWeights;
    }

    public final List<Triple<String, String, Double>> emissionProbabilities()
    {
        return _emissionWeights;
    }

    public final void setTrainingParam(boolean b)
    {
        this._isTrainingParam = b;
    }

    public final boolean hasTrainingParams()
    {
        return this._isTrainingParam;
    }
}
