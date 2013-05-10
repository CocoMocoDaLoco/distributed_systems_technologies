package dst.ass1.jpa.model.impl;

import java.io.Serializable;

import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IUser;

public class MembershipKey implements IMembershipKey, Serializable {
    private static final long serialVersionUID = 1L;

    private IUser user;
    private IGrid grid;

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public IGrid getGrid() {
        return grid;
    }

    @Override
    public void setGrid(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (!(that instanceof MembershipKey)) {
            return false;
        }

        MembershipKey thatKey = (MembershipKey)that;

        boolean isUserEqual = (user == null) ? thatKey.user == null : user.equals(thatKey.user);
        boolean isGridEqual = (grid == null) ? thatKey.grid == null : grid.equals(thatKey.grid);

        return (isUserEqual && isGridEqual);
    }

    @Override
    public int hashCode() {
        if (user == null) {
            return 0;
        }

        int hashCode = user.hashCode();

        if (grid != null) {
            hashCode ^= grid.hashCode();
        }

        return hashCode;
    }

}
