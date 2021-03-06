package de.chkal.togglz.core.manager;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.chkal.togglz.core.Feature;
import de.chkal.togglz.core.annotation.EnabledByDefault;
import de.chkal.togglz.core.config.FeatureManagerConfiguration;
import de.chkal.togglz.core.repository.FeatureStateRepository;
import de.chkal.togglz.core.repository.mem.InMemoryRepository;
import de.chkal.togglz.core.user.FeatureUser;
import de.chkal.togglz.core.user.FeatureUserProvider;
import de.chkal.togglz.core.user.SimpleFeatureUser;

public class DefaultFeatureManagerTest {

    private FeatureStateRepository repository;
    private FeatureManager manager;
    private TestFeatureUserProvider featureUserProvider;

    @Before
    public void before() {

        repository = new InMemoryRepository();
        repository.setFeatureState(new FeatureState(MyFeatures.DELETE_USERS, true, Arrays.asList("admin")));
        repository.setFeatureState(new FeatureState(MyFeatures.EXPERIMENTAL, false));

        featureUserProvider = new TestFeatureUserProvider();

        manager = new DefaultFeatureManager(new MyConfiguration(featureUserProvider));

    }

    @After
    public void after() {
        repository = null;
        manager = null;
        featureUserProvider = null;
    }

    @Test
    public void testGetFeatures() {
        assertEquals(2, manager.getFeatures().length);
        assertEquals(MyFeatures.DELETE_USERS, manager.getFeatures()[0]);
        assertEquals(MyFeatures.EXPERIMENTAL, manager.getFeatures()[1]);
    }

    @Test
    public void testIsActive() {

        // DELETE_USERS disabled for unknown user
        featureUserProvider.setFeatureUser(null);
        assertEquals(false, manager.isActive(MyFeatures.DELETE_USERS));

        // DELETE_USERS enabled for admin user
        featureUserProvider.setFeatureUser(new SimpleFeatureUser("admin", false));
        assertEquals(true, manager.isActive(MyFeatures.DELETE_USERS));

        // DELETE_USERS enabled for other user
        featureUserProvider.setFeatureUser(new SimpleFeatureUser("somebody", false));
        assertEquals(false, manager.isActive(MyFeatures.DELETE_USERS));

        // EXPERIMENTAL disabled for all
        featureUserProvider.setFeatureUser(null);
        assertEquals(false, manager.isActive(MyFeatures.EXPERIMENTAL));

    }

    @Test
    public void testGetFeatureState() {

        FeatureState state = manager.getFeatureState(MyFeatures.DELETE_USERS);
        assertEquals(MyFeatures.DELETE_USERS, state.getFeature());
        assertEquals(true, state.isEnabled());
        assertEquals(Arrays.asList("admin"), state.getUsers());

    }

    /**
     * Configuration for the {@link FeatureManager}
     */
    private final class MyConfiguration implements FeatureManagerConfiguration {

        private final FeatureUserProvider featureUserProvider;

        public MyConfiguration(FeatureUserProvider featureUserProvider) {
            this.featureUserProvider = featureUserProvider;
        }

        public Class<? extends Feature> getFeatureClass() {
            return MyFeatures.class;
        }

        public FeatureStateRepository getFeatureStateRepository() {
            return repository;
        }

        @Override
        public FeatureUserProvider getFeatureUserProvider() {
            return featureUserProvider;
        }

    }

    /**
     * {@link FeatureUserProvider} that allows to set the user directly
     */
    private final class TestFeatureUserProvider implements FeatureUserProvider {

        private FeatureUser featureUser;

        public void setFeatureUser(FeatureUser featureUser) {
            this.featureUser = featureUser;
        }

        @Override
        public FeatureUser getCurrentUser() {
            return featureUser;
        }

    }

    /**
     * Feature under test
     */
    private static enum MyFeatures implements Feature {

        @EnabledByDefault
        DELETE_USERS,

        EXPERIMENTAL;

    }

}
