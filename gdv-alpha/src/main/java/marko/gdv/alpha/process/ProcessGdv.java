package marko.gdv.alpha.process;

public interface ProcessGdv<I, O> {
    O start(Object input);
}
