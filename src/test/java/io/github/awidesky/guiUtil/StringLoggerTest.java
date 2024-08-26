package io.github.awidesky.guiUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import io.github.awidesky.guiUtil.simple.StringLogger;
import io.github.awidesky.guiUtil.simple.SynchronizedStringLogger;

class StringLoggerTest {

	@Test
	void synchronizedTest() {
		System.out.println("\n==========================StringLoggerTest==========================");
		Set<String> msgs = IntStream.range(0, 30).mapToObj(Integer::toString).collect(Collectors.toCollection( LinkedHashSet::new));
		StringLogger s1 = new StringLogger();
		StringLogger s2 = new SynchronizedStringLogger();
		
		msgs.stream().forEach(s1::info);
		msgs.parallelStream().forEach(s2::info);
		
		List<String> out = new LinkedList<>();
		Set<String> s1_res = s1.getString().lines().peek(out::add).collect(Collectors.toSet());
		System.out.println("[Unsynchronized] :");
		System.out.println(out.stream().collect(Collectors.joining(", ")));
		out.clear();
		Set<String> s2_res = s2.getString().lines().peek(out::add).collect(Collectors.toSet());
		System.out.println();
		System.out.println("[Synchronized]   :"); 
		System.out.println(out.stream().collect(Collectors.joining(", ")));

		s1.close(); s2.close();
		
		assertEquals(msgs, s1_res);
		assertEquals(msgs, s2_res);
		System.out.println("==========================StringLoggerTest==========================");
	}

}
