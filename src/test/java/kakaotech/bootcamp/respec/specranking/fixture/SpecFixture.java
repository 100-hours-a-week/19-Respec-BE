package kakaotech.bootcamp.respec.specranking.fixture;

import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import org.mockito.Mockito;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.mockito.Mockito.lenient;

public class SpecFixture {

    public static Spec createMockSpec() {
        return createMockSpec(DEFAULT_SPEC_ID);
    }

    public static Spec createMockSpec(Long specId) {
        Spec spec = Mockito.mock(Spec.class);
        lenient().when(spec.getId()).thenReturn(specId);
        return spec;
    }

    private SpecFixture() { }
}
