package utility;

public class Pair<S> {
	S first, second;
	
	public Pair(){
		first = null;
		second = null;
	}
	
	public Pair(S first, S second){
		this.first = first;
		this.second = second;
	}
	
	public S getFirst(){
		return first;
	}
	
	public S getSecond(){
		return second;
	}
	
	public void setFirst(S first){
		this.first = first;
	}
	
	public void setSecond(S second){
		this.second = second;
	}
	
	@Override
	public int hashCode(){
		return first.hashCode() * second.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null) return false;
		if(!(other instanceof Pair)) return false;
		@SuppressWarnings("unchecked")
		Pair<S> o = (Pair<S>) other;
		
		if(first.equals(o.first) && second.equals(o.second)){
			return true;
		}else if(first.equals(o.second) && second.equals(o.first)){
			return true;
		}
		return false;
	}
}
