package manager;

import org.junit.jupiter.api.BeforeEach;


class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    @BeforeEach
    public void createManager() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

}