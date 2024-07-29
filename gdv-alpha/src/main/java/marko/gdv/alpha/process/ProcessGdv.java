package marko.gdv.alpha.process;

public interface ProcessGdv<I, O> {
    O start(I input);
}
