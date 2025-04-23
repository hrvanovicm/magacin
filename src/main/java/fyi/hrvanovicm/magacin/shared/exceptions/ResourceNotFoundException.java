package fyi.hrvanovicm.magacin.shared.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(Class<?> resource, Long id) {
        super(String.format("Resource %s with %d ID not found", resource.getSimpleName(), id));
    }
}
