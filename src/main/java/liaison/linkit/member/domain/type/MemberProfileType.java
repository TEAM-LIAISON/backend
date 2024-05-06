package liaison.linkit.member.domain.type;

public enum MemberProfileType {
    NO_PERMISSION,
    PROFILE_OPEN_PERMISSION,
    MATCHING_PERMISSION;

    public static MemberProfileType openAndClosePermission(final boolean isOpen) {
        if (isOpen) {
            return PROFILE_OPEN_PERMISSION;
        }
        return NO_PERMISSION;
    }

    public static MemberProfileType changeAndOpenPermission(final boolean isMatching) {
        if (isMatching) {
            return MATCHING_PERMISSION;
        }
        return PROFILE_OPEN_PERMISSION;
    }

}
