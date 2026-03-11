package catan;

import java.util.List;

public interface Observer {
	void update(Board b, List<Agent> agents);
}
