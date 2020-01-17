package minedreams.mi.api.net.message;

import minedreams.mi.api.net.info.SimpleImplInfo;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface TaskInfo {
	
	void run(String key, SimpleImplInfo<?> info);
	
}
