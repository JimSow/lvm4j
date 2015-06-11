package net.digital_alexandria.sshmm.hmm;

import net.digital_alexandria.sshmm.util.File;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.digital_alexandria.sshmm.util.String.toDouble;

/**
 * @author Simon Dirmeier
 * @email simon.dirmeier@gmx.de
 * @date 09/06/15
 * @desc HMM class for training and prediction. Can be used to predict a
 * sequence of latent states for a given sequence of observations.
 */
public class HMM
{
	protected final List<State>       _STATES;
	protected final List<Observation> _OBSERVATIONS;
	protected final List<Transition>  _TRANSITIONS;
	protected final List<Emission>    _EMISSIONS;

	protected HMM(String hmmFile)
	{
		this._STATES = new ArrayList<>();
		this._OBSERVATIONS = new ArrayList<>();
		this._TRANSITIONS = new ArrayList<>();
		this._EMISSIONS = new ArrayList<>();
		init(hmmFile);
	}

	private void init(String hmmFile)
	{
		SAXBuilder builder = new SAXBuilder();
		Document document = null;
		try
		{
			document = (Document) builder.build(hmmFile);
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Element rootNode = document.getRootElement();
		Element meta = rootNode.getChild("meta");

		char states[] = meta.getChild("states").getValue().toCharArray();
		char observations[] = meta.getChild("observations").getValue().toCharArray();
		int order = Integer.parseInt(meta.getChild("order").getValue());

		for (char s : states) System.out.println(s);
		for (char s : observations) System.out.println(s);
		System.out.println(order);
		System.exit(1);
		BufferedReader bR;
		double transitions[][] = new double[0][0];
		double emissions[][] = new double[0][0];
		double[] probs = new double[0];
		try
		{
			bR = File.getReader(hmmFile);
			String line;
			while ((line = bR.readLine()) != null)
			{
				if (line.startsWith("#States"))
					states = bR.readLine().toUpperCase().trim().toCharArray();
				else if (line.startsWith("#Transitions"))
				{
					ArrayList<String> l = new ArrayList<>();
					for (int i = 0; i < states.length; i++)
					{
						String tr = bR.readLine();
						if (tr.startsWith("#"))
							net.digital_alexandria.sshmm.util.System.exit("Error " +
																	"while " +
																	"parsing" +
																	" " +
																	"transition matrix",
																	-1);
						l.add(tr);
					}
					transitions = initTransitionMatrix(l);
				}
				else if (line.startsWith("#Observations"))
					observations = bR.readLine().toUpperCase().trim()
									 .toCharArray();
				else if (line.startsWith("#Emissions"))
				{
					ArrayList<String> l = new ArrayList<>();
					for (int i = 0; i < states.length; i++)
					{
						String tr = bR.readLine();
						if (tr.startsWith("#"))
							net.digital_alexandria.sshmm.util.System.exit("Error " +
																	"while " +
																	"parsing" +
																	" " +
																	"emission" +
																	" " +
																	"matrix",
																	-1);
						l.add(tr);
					}
					emissions = initEmissionMatrix(states, l);
				}
				else if (line.startsWith("#StartingProbabilities"))
				{
					probs = toDouble(bR.readLine().split("\t"));
				}
				else
					net.digital_alexandria.sshmm.util.System.exit("Unrecognized " +
															"pattern at " +
															"parsing hmm " +
															"file!", -1);
			}
			bR.close();
		}
		catch (IOException e)
		{
			Logger.getLogger(HMMFactory.class.getSimpleName()).
				log(Level.WARNING, "Could not read HMM-file\n" + e.toString());
			net.digital_alexandria.sshmm.util.System.exit("", -1);
		}
		init(states, observations, transitions, emissions, probs);
	}

	private void init(char[] states, char[] observations,
					  double[][] transitions, double[][] emissions, double[] probs)
	{
		addStates(states);
		addObservations(observations);
		addStartingProbabilities(probs);
		addTransitions(transitions);
		addEmissions(emissions);
	}

	private void addStartingProbabilities(double[] probs)
	{
		if (probs.length != _STATES.size()) return;
		for (int i = 0; i < _STATES.size(); i++)
			_STATES.get(i).startingStateProbability(probs[i]);
	}

	private void addStates(char[] states)
	{
		for (int i = 0; i < states.length; i++)
			_STATES.add(new State(states[i], i));
	}

	private void addObservations(char[] observations)
	{
		for (int i = 0; i < observations.length; i++)
			_OBSERVATIONS.add(new Observation(observations[i], i));
	}

	private void addTransitions(double[][] transitions)
	{
		for (int i = 0; i < transitions.length; i++)
		{
			State source = _STATES.get(i);
			for (int j = 0; j < transitions[i].length; j++)
			{
				if (transitions[i][j] == 0.0) continue;
				State sink = _STATES.get(j);
				addTransition(source, sink, transitions[i][j]);
			}
		}
	}

	private void addTransition(State source, State sink, double transition)
	{
		Transition t = new Transition(source, sink, (transition));
		_TRANSITIONS.add(t);
		source.addTransition(t);
	}

	private void addEmissions(double[][] emissions)
	{
		for (int i = 0; i < emissions.length; i++)
		{
			State source = _STATES.get(i);
			for (int j = 0; j < emissions[i].length; j++)
			{
				if (emissions[i][j] == 0.0) continue;
				Observation sink = _OBSERVATIONS.get(j);
				addEmission(source, sink, (emissions[i][j]));
			}
		}
	}

	private void addEmission(State source, Observation sink, double emission)
	{
		Emission e = new Emission(source, sink, emission);
		_EMISSIONS.add(e);
		source.addEmission(e);
	}

	private double[][] initEmissionMatrix(char[] states, ArrayList<String>
		list)
	{
		double[][] emissions = new double[states.length][];
		for (int i = 0; i < emissions.length; i++)
			emissions[i] = toDouble(list.get(i).split
				("\t"));
		return emissions;
	}

	private double[][] initTransitionMatrix(ArrayList<String> l)
	{
		double m[][] = new double[l.size()][];
		for (int i = 0; i < m.length; i++)
			m[i] = toDouble(l.get(i).split("\t"));
		return m;
	}

	public double[] logStartProbabilities()
	{
		double[] probs = new double[this._STATES.size()];
		final double pseudo = 0.000001;
		for (State s : _STATES)
			probs[s.idx()] = Math.log(s.startingStateProbability() + pseudo);
		return probs;
	}

	public double[] startProbabilities()
	{
		double[] probs = new double[this._STATES.size()];
		for (State s : _STATES)
			probs[s.idx()] = (s.startingStateProbability());
		return probs;
	}

	public double[][] logEmissionMatrix()
	{
		double[][] emissionMatrix =
			new double[this._STATES.size()][this._OBSERVATIONS.size()];
		final double pseudo = 0.000001;
		for (Emission e : _EMISSIONS)
		{
			emissionMatrix[e.source().idx()][e.sink().idx()] =
				Math.log(e.emissionProbability() + pseudo);
		}
		return emissionMatrix;
	}

	public double[][] emissionMatrix()
	{
		double[][] emissionMatrix =
			new double[this._STATES.size()][this._OBSERVATIONS.size()];
		for (Emission e : _EMISSIONS)
		{
			emissionMatrix[e.source().idx()][e.sink().idx()] =
				(e.emissionProbability());
		}
		return emissionMatrix;
	}

	public double[][] logTransitionMatrix()
	{
		double[][] transitionsMatrix =
			new double[this._STATES.size()][this._STATES.size()];
		final double pseudo = 0.000001;
		for (Transition t : _TRANSITIONS)
			transitionsMatrix[t.source().idx()][t.sink().idx()] =
				Math.log(t.transitionProbability() + pseudo);
		return transitionsMatrix;
	}

	public double[][] transitionMatrix()
	{
		double[][] transitionsMatrix =
			new double[this._STATES.size()][this._STATES.size()];
		for (Transition t : _TRANSITIONS)
			transitionsMatrix[t.source().idx()][t.sink().idx()] =
				(t.transitionProbability());
		return transitionsMatrix;
	}

	public List<Transition> transitions()
	{
		return _TRANSITIONS;
	}

	public List<Emission> emissions()
	{
		return _EMISSIONS;
	}

	public List<State> states()
	{
		return _STATES;
	}

	public void write(String hmmFile)
	{
		HMMWriter writer = HMMWriter.getInstance();
		writer.write(this, hmmFile);
	}

	public List<Observation> observations()
	{
		return _OBSERVATIONS;
	}
}
