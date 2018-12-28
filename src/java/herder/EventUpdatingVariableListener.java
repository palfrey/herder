package herder.solver.event;

import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class EventUpdatingVariableListener extends VariableListenerAdapter<Event>  {
    @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, Event event) {
            Event chained = event.getChainedEvent();
            if (chained != null) {
                scoreDirector.afterVariableChanged(chained, "slot");
            }
            Event laterEvent = event.getLaterEvent();
            if (laterEvent != null) {
                scoreDirector.afterVariableChanged(laterEvent, "slot");
            }
        }
}
