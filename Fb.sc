
Fb {

	*delayUGen { ^DelayN }

	*new {
		arg func, maxdelaytime, delaytime=maxdelaytime, channels;
		var buf, phase, frames, sig, adddelay;
		if (maxdelaytime.isNil) {
			adddelay = false;
		} {
			adddelay = true;
			maxdelaytime = maxdelaytime - ControlDur.ir;
			delaytime = delaytime - ControlDur.ir;
		};
		channels = channels ?? {func.value(Silent.ar(1)).asArray.size};
		channels = channels max: maxdelaytime.asArray.size max: delaytime.asArray.size;
		frames = ControlDur.ir*SampleRate.ir;
		buf = LocalBuf(frames,channels).clear;
		phase = Phasor.ar(0,1,0,frames);

		sig = func.value(BufRd.ar(channels,buf,phase));
		if (adddelay) {sig = this.delayUGen.ar(sig, maxdelaytime, delaytime)};
		BufWr.ar(sig, buf, phase);
		^sig;
	}
}

FbL : Fb {*delayUGen{^DelayL}}

FbC : Fb {*delayUGen{^DelayC}}
